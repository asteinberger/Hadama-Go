	public class Pair<L, R> {

		private L left;
		private R right;

		public Pair(L l, R r) {
			this.left = l;
			this.right = r;
		} // end constructor

		public L getLeft() {
			return this.left;
		} // end getLeft()

		public R getRight() {
			return this.right;
		} // end getRight()

	} // end class