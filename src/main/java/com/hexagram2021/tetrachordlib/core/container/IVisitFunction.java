package com.hexagram2021.tetrachordlib.core.container;

@SuppressWarnings("unused")
public interface IVisitFunction {
	interface Simple<T> extends IVisitFunction {
		void visit(T obj);
	}
	interface Mapped<T, TR> extends IVisitFunction {
		TR visit(T obj);
	}
	interface Binary<T1, T2> extends IVisitFunction {
		void visit(T1 obj1, T2 obj2);
	}
	interface MappedBinary<T1, T2, TR> extends IVisitFunction {
		TR visit(T1 obj1, T2 obj2);
	}
}
