; checks the sorts of bit-vector functions
(set-logic QF_BV)
(declare-fun x () (_ BitVec 3))
(declare-fun y () (_ BitVec 5))
(declare-fun z () (_ BitVec 8))
(assert (= (bvnot x) #b1011))
(assert (= (bvnot true) #b1011))
(assert (= (bvnot x y) #b1011))
(assert (= (bvneg x) #b0111))
(assert (= (bvneg x y) #b0111))
(assert (= (bvand true y) #b111))
(assert (= (bvand x false) #b111))
(assert (= (bvand x y) #b111))
(assert (= (bvand x y z) #b111))
(assert (= (bvor x y) #b111))
(assert (= (bvadd x y) #b111))
(assert (= (bvmul x y) #b111))
(assert (= (bvshl x y) #b111))
(assert (= (bvlshr x y) #b111))
(assert (= (bvudiv x y) #b111))
(assert (= (bvurem x y) #b111))
(assert (bvult x y))
(assert (bvult y))
(assert (bvult true y))
(assert (bvult x false))
(assert (= (concat x y) #xa))
(assert (= (concat y) #xaa))
(assert (= (concat x true) z))
(assert (= ((_ extract 2 40) z) x))
(assert (= ((_ extract 2 1) z) x))
(assert (= ((_ extract a 1) z) x))
(assert (= ((_ extract ) z) x))
(assert (= ((_ extract 1 2 3 ) z) x))
(assert (= #b101 #b1010))
(assert (distinct #b1 #b10))
(assert (ite true #b0 #b11))
