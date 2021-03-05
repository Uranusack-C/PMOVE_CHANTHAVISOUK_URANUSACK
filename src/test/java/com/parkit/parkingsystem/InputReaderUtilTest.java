package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.InputReaderUtil;

public class InputReaderUtilTest {

	private InputReaderUtil inputReaderUtil;

	@BeforeEach 
	private void setUpPerTest() {
		inputReaderUtil = new InputReaderUtil();
	}

	@Test
	public void whenReaderUtilReturnAIntUpperThanZero() throws Exception {
		Scanner scan = new Scanner(new ByteArrayInputStream("1".getBytes()));
		inputReaderUtil.setScan(scan);
		assertEquals(1, inputReaderUtil.readSelection());
	}	
 
	@Test
	public void whenReaderUtilReturnAString() throws Exception {
		Scanner scan = new Scanner(new ByteArrayInputStream("test".getBytes()));
		inputReaderUtil.setScan(scan);
		assertEquals(-1, inputReaderUtil.readSelection());
	}	

	@Test
	public void whenRreadVehiculeRegistrationNumbereturnAString() throws Exception {
		Scanner scan = new Scanner(new ByteArrayInputStream("test".getBytes()));
		inputReaderUtil.setScan(scan);
		assertEquals("test", inputReaderUtil.readVehiculeRegistrationNumber());
	}

	@Test
	public void whenRreadVehiculeRegistrationNumbereturnASpecialCharact() throws Exception {
		Scanner scan = new Scanner(new ByteArrayInputStream("\n".getBytes()));
		inputReaderUtil.setScan(scan);
		assertThrows(IllegalArgumentException.class , () -> inputReaderUtil.readVehiculeRegistrationNumber());

	}

}

