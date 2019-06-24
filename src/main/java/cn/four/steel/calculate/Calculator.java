package cn.four.steel.calculate;

public class Calculator {
	
	private static Calculator calculator = null;
	private Calculator(){
	}
	
	public static Calculator getCalculator(){
		if( null == calculator){
			calculator = new Calculator();
		}
		return null;
	}
	public void schedule(){
		
	}
}
