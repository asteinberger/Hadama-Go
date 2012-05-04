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

		public void setLeft(L left) {
			this.left = left;
		} // end setLeft()

		public R getRight() {
			return this.right;
		} // end getRight()

		public void setRight(R right) {
			this.right = right;
		} // end setRight()

	} // end class