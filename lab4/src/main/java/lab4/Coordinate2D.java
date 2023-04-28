package lab4;

import java.util.Objects;


public class Coordinate2D implements Comparable<Coordinate2D> {
	protected final double x;
	protected final double y;
	
	public Coordinate2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Coordinate2D o) {
		return Double.compare(this.x, o.x);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Coordinate2D that)) return false;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
