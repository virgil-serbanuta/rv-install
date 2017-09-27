import qualified Data.Map as Map

data Pgm = Pgm Ids Stmt deriving Show
data Ids = Ids [String] deriving Show
data Stmt = EmptyStmt | 
            Assign String Aexp | 
            Sequence Stmt Stmt | 
            While Bexp Stmt | 
            If Bexp Stmt Stmt 
            deriving Show
data Aexp = Int Integer | Id String | Add Aexp Aexp | Div Aexp Aexp deriving Show
data Bexp = Boolean Bool | And Bexp Bexp | Not Bexp | LessOrEquals Aexp Aexp deriving Show

data KItem = DivLeftMissing Aexp | 
             DivRightMissing Aexp | 
             AddLeftMissing Aexp | 
             AddRightMissing Aexp | 
             LessOrEqualsLeftMissing Aexp | 
             LessOrEqualsRightMissing Aexp |
             NotOperandMissing |
             AssignmentRightMissing String |
             IfConditionMissing Stmt Stmt |
             AndLeftMissing Bexp
             deriving Show

data KThing = KThingPgm Pgm | 
              KThingStmt Stmt | 
              KThingAexp Aexp | 
              KThingBexp Bexp | 
              KThingKItem KItem 
              deriving Show

data KCell = KCell [KThing] deriving Show
data StateCell = StateCell (Map.Map String Aexp) deriving Show
data Configuration = Configuration KCell StateCell deriving Show

isInt (Int i) = True
isInt _ = False

isBool (Boolean b) = True
isBool _ = False

step (Configuration (KCell k) (StateCell state)) = 
    case k of
        [] -> Nothing
        (firstK : kReminder) -> (
            case (firstK, kReminder) of
                {- Structural -}
                (KThingStmt EmptyStmt, _) -> Just (Configuration(KCell kReminder) (StateCell state))
                (KThingStmt (Sequence first second), _) ->
                    Just (Configuration(KCell ((KThingStmt first) : (KThingStmt second) : kReminder)) (StateCell state))
                (KThingStmt (While condition body), _) ->
                    Just (Configuration
                        (KCell (KThingStmt (If condition (Sequence body (While condition body)) EmptyStmt) : kReminder))
                        (StateCell state))
                (KThingPgm (Pgm (Ids []) stmt), _) -> Just (Configuration(KCell (KThingStmt stmt : kReminder)) (StateCell state))

                {- Non-structural -}
                (KThingAexp (Id id), _) -> case (Map.lookup id state) of
                    Nothing -> Nothing  {- This is cheating, not matching should fall through -}
                    Just value -> Just(Configuration(KCell (KThingAexp value : kReminder)) (StateCell state))
                (KThingAexp (Div (Int x) (Int y)), _) | y /= 0 -> 
                    Just(Configuration (KCell ((KThingAexp (Int (quot x y))) : kReminder)) (StateCell state))
                (KThingAexp (Add (Int x) (Int y)), _) -> 
                    Just(Configuration (KCell ((KThingAexp (Int (x + y))) : kReminder)) (StateCell state))
                (KThingBexp (LessOrEquals (Int x) (Int y)), _) -> 
                    Just(Configuration (KCell ((KThingBexp (Boolean (x <= y))) : kReminder)) (StateCell state))
                (KThingBexp (Not (Boolean x)), _) -> 
                    Just(Configuration (KCell ((KThingBexp (Boolean (not x))) : kReminder)) (StateCell state))
                (KThingBexp (And (Boolean True) y), _) -> 
                    Just(Configuration (KCell ((KThingBexp y) : kReminder)) (StateCell state))
                (KThingBexp (And (Boolean False) _), _) -> 
                    Just(Configuration (KCell ((KThingBexp (Boolean False)) : kReminder)) (StateCell state))
                (KThingStmt (Assign name (Int value)), _) -> 
                    Just(Configuration (KCell kReminder) (StateCell (Map.insert name (Int value) state)))
                (KThingStmt (If (Boolean True) ithen _), _) -> 
                    Just(Configuration (KCell (KThingStmt ithen : kReminder)) (StateCell state))
                (KThingStmt (If (Boolean False) _ ielse), _) -> 
                    Just(Configuration (KCell (KThingStmt ielse : kReminder)) (StateCell state))
                (KThingPgm (Pgm (Ids (firstId:idsReminder)) stmt), _) -> Just (Configuration
                    (KCell ((KThingPgm (Pgm (Ids idsReminder) stmt)) : kReminder)) 
                    (StateCell (Map.insert firstId (Int 0) state)))

                {- Expresion decomposition and rebuilding (aka heating and cooling). -}
                (KThingAexp (Div x y), _) | (not (isInt x)) -> 
                    Just(Configuration (KCell ((KThingAexp x) : (KThingKItem (DivLeftMissing y)) : kReminder)) (StateCell state))
                (KThingAexp (Int x), ((KThingKItem (DivLeftMissing y)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingAexp (Div (Int x) y)) : kSecondReminder)) (StateCell state))
                (KThingAexp (Div x y), _) | (isInt x) && (not (isInt y)) -> 
                    Just(Configuration (KCell ((KThingAexp y) : (KThingKItem (DivRightMissing x)) : kReminder)) (StateCell state))
                (KThingAexp (Int y), ((KThingKItem (DivRightMissing x)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingAexp (Div x (Int y))) : kSecondReminder)) (StateCell state))

                (KThingAexp (Add x y), _) | (not (isInt x)) -> 
                    Just(Configuration (KCell ((KThingAexp x) : (KThingKItem (AddLeftMissing y)) : kReminder)) (StateCell state))
                (KThingAexp (Int x), ((KThingKItem (AddLeftMissing y)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingAexp (Add (Int x) y)) : kSecondReminder)) (StateCell state))
                (KThingAexp (Add x y), _) | (isInt x) && (not (isInt y)) -> 
                    Just(Configuration (KCell ((KThingAexp y) : (KThingKItem (AddRightMissing x)) : kReminder)) (StateCell state))
                (KThingAexp (Int y), ((KThingKItem (AddRightMissing x)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingAexp (Add x (Int y))) : kSecondReminder)) (StateCell state))

                (KThingBexp (LessOrEquals x y), _) | (not (isInt x)) -> 
                    Just(Configuration 
                        (KCell ((KThingAexp x) : (KThingKItem (LessOrEqualsLeftMissing y)) : kReminder)) 
                        (StateCell state))
                (KThingAexp (Int x), ((KThingKItem (LessOrEqualsLeftMissing y)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingBexp (LessOrEquals (Int x) y)) : kSecondReminder)) (StateCell state))
                (KThingBexp (LessOrEquals x y), _) | (isInt x) && (not (isInt y)) -> 
                    Just(Configuration 
                        (KCell ((KThingAexp y) : (KThingKItem (LessOrEqualsRightMissing x)) : kReminder)) 
                        (StateCell state))
                (KThingAexp (Int y), ((KThingKItem (LessOrEqualsRightMissing x)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingBexp (LessOrEquals x (Int y))) : kSecondReminder)) (StateCell state))

                (KThingBexp (Not x), _) | (not (isBool x)) -> 
                    Just(Configuration (KCell ((KThingBexp x) : (KThingKItem NotOperandMissing) : kReminder)) (StateCell state))
                (KThingBexp (Boolean x), ((KThingKItem NotOperandMissing) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingBexp (Not (Boolean x))) : kSecondReminder)) (StateCell state))

                (KThingStmt (Assign id aexp), _) | (not (isInt aexp)) -> 
                    Just(Configuration
                        (KCell ((KThingAexp aexp) : (KThingKItem (AssignmentRightMissing id)) : kReminder)) 
                        (StateCell state))
                (KThingAexp (Int x), ((KThingKItem (AssignmentRightMissing id)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingStmt (Assign id (Int x))) : kSecondReminder)) (StateCell state))

                (KThingStmt (If condition ithen ielse), _) | (not (isBool condition)) -> 
                    Just(Configuration
                        (KCell ((KThingBexp condition) : (KThingKItem (IfConditionMissing ithen ielse)) : kReminder)) 
                        (StateCell state))
                (KThingBexp (Boolean x), ((KThingKItem (IfConditionMissing ithen ielse)) : kSecondReminder)) -> 
                    Just(Configuration (KCell ((KThingStmt (If (Boolean x) ithen ielse)) : kSecondReminder)) (StateCell state))

                _ -> Nothing)

steps 0 configuration = configuration
steps n configuration = (
    case (step configuration) of
        Nothing -> configuration
        Just nextConfiguration -> steps (n-1) nextConfiguration)

run configuration = (
    case (step configuration) of
        Nothing -> configuration
        Just nextConfiguration -> run nextConfiguration)

pgm = Pgm
    (Ids ["n", "sum"])
    (Sequence
        (Assign "n" (Int 10000000))
        (Sequence
            (Assign "sum" (Int 0))
            (While
                (Not (LessOrEquals (Id "n") (Int 0)))
                (Sequence
                    (Assign "sum" (Add (Id "sum") (Id "n")))
                    (Assign "n" (Add (Id "n") (Int (-1))))
                )
            )
        )
    )

configuration = Configuration (KCell [KThingPgm pgm]) (StateCell Map.empty)
{- main = putStrLn (show (steps 3000 configuration)) -}
main = putStrLn (show (run configuration))
