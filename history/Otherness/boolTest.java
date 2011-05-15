public class boolTest{
	private static void changeBool(Boolean value){
		value=new Boolean(true);
	}
	public static void main(String[]args){
		Boolean value=null;
		boolTest.changeBool(value);
		System.out.println(value);
	}

}
