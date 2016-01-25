package main.java.genutils;

public class PredictResult implements Comparable<PredictResult> {

	private String  combo;
	private double preRat;

	public PredictResult(String item, double sim) {
		this.combo = item;
		this.preRat = sim;
	}


	public String getItem() {
		return combo;
	}

	public void setItem(String item) {
		this.combo = item;
	}

	public double getSimilar() {
		return preRat;
	}

	public void setSimilar(double similar) {
		this.preRat = similar;
	}

	public int compareTo(PredictResult o) {
		return -((Double) this.preRat).compareTo(o.preRat);
	}

}
