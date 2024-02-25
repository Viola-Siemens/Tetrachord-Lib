package com.hexagram2021.tetrachordlib.core.container.impl;

import com.hexagram2021.tetrachordlib.core.container.IEditRule;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("unused")
public final class EditRules {
	private EditRules() {
	}

	/**
	 * delta should only be 1 or 0.
	 */
	public static final class Boolean {
		private Boolean() {
		}

		public interface Count extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override @Nullable
			default java.lang.Integer zero() {
				return null;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta * length;
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta * xLength * yLength;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return a + b;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return lu + ru + ld + rd;
			}
			@Override @Nullable
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return delta == null ? old : delta;
			}
		}

		public static Count count() {
			return new Count() {};
		}
	}

	public static final class Integer {
		private Integer() {
		}

		public interface SumAdd extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override
			default java.lang.Integer zero() {
				return 0;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				return x + Objects.requireNonNull(delta) * length;
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta) * xLength * yLength;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return a + b;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return lu + ru + ld + rd;
			}
			@Override
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface MaxAdd extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override
			default java.lang.Integer zero() {
				return 0;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return Math.max(a, b);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return Math.max(Math.max(lu, ru), Math.max(ld, rd));
			}
			@Override
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface MinAdd extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override
			default java.lang.Integer zero() {
				return 0;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return Math.min(a, b);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return Math.min(Math.min(lu, ru), Math.min(ld, rd));
			}
			@Override
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface SumSet extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override @Nullable
			default java.lang.Integer zero() {
				return null;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta * length;
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta * xLength * yLength;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return a + b;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return lu + ru + ld + rd;
			}
			@Override @Nullable
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return delta == null ? old : delta;
			}
		}
		public interface MaxSet extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override @Nullable
			default java.lang.Integer zero() {
				return null;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return Math.max(a, b);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return Math.max(Math.max(lu, ru), Math.max(ld, rd));
			}
			@Override @Nullable
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return delta == null ? old : delta;
			}
		}
		public interface MinSet extends IEditRule<java.lang.Integer> {
			@Override
			default java.lang.Integer elementDefault() {
				return 0;
			}
			@Override @Nullable
			default java.lang.Integer zero() {
				return null;
			}

			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Integer edit(java.lang.Integer x, @Nullable java.lang.Integer delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer a, java.lang.Integer b) {
				return Math.min(a, b);
			}
			@Override
			default java.lang.Integer combine(java.lang.Integer lu, java.lang.Integer ru, java.lang.Integer ld, java.lang.Integer rd) {
				return Math.min(Math.min(lu, ru), Math.min(ld, rd));
			}
			@Override @Nullable
			default java.lang.Integer update(@Nullable java.lang.Integer old, @Nullable java.lang.Integer delta) {
				return delta == null ? old : delta;
			}
		}

		public static SumAdd sumAdd() {
			return new SumAdd() {};
		}

		public static MaxAdd maxAdd() {
			return new MaxAdd() {};
		}
		public static MinAdd minAdd() {
			return new MinAdd() {};
		}
		public static SumSet sumSet() {
			return new SumSet() {};
		}
		public static MaxSet maxSet() {
			return new MaxSet() {};
		}
		public static MinSet minSet() {
			return new MinSet() {};
		}
	}

	public static final class Double {
		private Double() {
		}

		public interface SumAdd extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override
			default java.lang.Double zero() {
				return 0.0D;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				return x + Objects.requireNonNull(delta) * length;
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta) * xLength * yLength;
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return a + b;
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return lu + ru + ld + rd;
			}
			@Override
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface MaxAdd extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override
			default java.lang.Double zero() {
				return 0.0D;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return Math.max(a, b);
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return Math.max(Math.max(lu, ru), Math.max(ld, rd));
			}
			@Override
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface MinAdd extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override
			default java.lang.Double zero() {
				return 0.0D;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				return x + Objects.requireNonNull(delta);
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return Math.min(a, b);
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return Math.min(Math.min(lu, ru), Math.min(ld, rd));
			}
			@Override
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return Objects.requireNonNull(old) + Objects.requireNonNull(delta);
			}
		}
		public interface SumSet extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override @Nullable
			default java.lang.Double zero() {
				return null;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta * length;
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta * xLength * yLength;
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return a + b;
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return lu + ru + ld + rd;
			}
			@Override @Nullable
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return delta == null ? old : delta;
			}
		}
		public interface MaxSet extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override @Nullable
			default java.lang.Double zero() {
				return null;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return Math.max(a, b);
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return Math.max(Math.max(lu, ru), Math.max(ld, rd));
			}
			@Override @Nullable
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return delta == null ? old : delta;
			}
		}
		public interface MinSet extends IEditRule<java.lang.Double> {
			@Override
			default java.lang.Double elementDefault() {
				return 0.0D;
			}
			@Override @Nullable
			default java.lang.Double zero() {
				return null;
			}

			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int length) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Double edit(java.lang.Double x, @Nullable java.lang.Double delta, int xLength, int yLength) {
				if(delta == null) {
					return x;
				}
				return delta;
			}
			@Override
			default java.lang.Double combine(java.lang.Double a, java.lang.Double b) {
				return Math.min(a, b);
			}
			@Override
			default java.lang.Double combine(java.lang.Double lu, java.lang.Double ru, java.lang.Double ld, java.lang.Double rd) {
				return Math.min(Math.min(lu, ru), Math.min(ld, rd));
			}
			@Override @Nullable
			default java.lang.Double update(@Nullable java.lang.Double old, @Nullable java.lang.Double delta) {
				return delta == null ? old : delta;
			}
		}

		public static SumAdd sumAdd() {
			return new SumAdd() {};
		}

		public static MaxAdd maxAdd() {
			return new MaxAdd() {};
		}
		public static MinAdd minAdd() {
			return new MinAdd() {};
		}
		public static SumSet sumSet() {
			return new SumSet() {};
		}
		public static MaxSet maxSet() {
			return new MaxSet() {};
		}
		public static MinSet minSet() {
			return new MinSet() {};
		}
	}
}
