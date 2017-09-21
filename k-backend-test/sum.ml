let rec sum s n = if n > 0 then sum (s+n) (n-1) else s
let _ = print_int (sum 0 (int_of_string (Sys.argv.(1))));print_newline ()
