# Path to the directory k-distribution/target/release/k/
# inside a checkout of https://github.com/runtimeverification/k
# which has been successfully compiled,
# or the root directory of an unpacked snapshot
RV_PATH=/home/brandon/code/rvk/k-distribution/target/release/k
RV_KOMPILE:=$(RV_PATH)/bin/kompile
# Path to the unpacked K 4.0 release,
# or to the # k-distribution/target/release/k/
# directory inside a checkout of https://github.com/kframework/k
UIUC_PATH=/home/brandon/code/checkout/k-4.0
UIUC_KOMPILE:=$(UIUC_PATH)/bin/kompile

PROGS=imp-mlton imp-rvk imp-uiuck sum-c sum.opt

all: $(PROGS)
.PHONY: all clean distclean
clean :
	rm -f *.cmx *.cmi *.o *.hi
distclean : clean
	rm -rf $(PROGS)
%.jar : %.scala
	scalac -d $@ $^
%.opt : %.ml
	ocamlopt $^ -o $@
%-mlton : %.mlb %.sml
	mlton -default-type int64 -output $@ $<
.PHONY: imp-rvk imp-uiuck
imp-rvk : imp-rvk/imp-kompiled/timestamp
imp-rvk/imp-kompiled/timestamp : imp.k
	$(RV_KOMPILE) -O2 -d imp-rvk imp.k
imp-uiuck : imp-uiuck/imp-kompiled/timestamp
imp-uiuck/imp-kompiled/timestamp : imp.k
	$(UIUC_KOMPILE) -d imp-uiuck imp.k
imp-mlton : maps.sml
sum-c : sum.c
	gcc -O sum.c -o sum-c
