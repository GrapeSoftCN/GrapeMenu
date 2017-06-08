package test;

import httpServer.booter;

public class TestMenu {
	public static void main(String[] args) {
		booter booter = new booter();
		 try {
		 System.out.println("GrapeMenu!");
		 System.setProperty("AppName", "GrapeMenu");
		 booter.start(1003);
		 } catch (Exception e) {
		 }
	}
}
