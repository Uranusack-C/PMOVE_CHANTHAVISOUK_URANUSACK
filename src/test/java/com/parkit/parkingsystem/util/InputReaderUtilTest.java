package com.parkit.parkingsystem.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputReaderUtilTest {
	
	private InputReaderUtil inputReaderUtil;
	
	@BeforeEach 
	private void setUpPerTest() {
		inputReaderUtil = new InputReaderUtil();
	}
	
	@Test
	public void whenReadSelectionTestReturnAnIntUpperThanZero() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream("1".getBytes());
		
		
		int teset = inputReaderUtil.readSelection();
		assertEquals(teset, in);
	}
	
}
