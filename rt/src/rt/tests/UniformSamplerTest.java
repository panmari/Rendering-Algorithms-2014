package rt.tests;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import rt.samplers.UniformSampler;

public class UniformSamplerTest {

	private UniformSampler s;
	private float epsilon = 1e-9f;
	
	@Before
	public void setUp(){
		s = new UniformSampler();
	}
	
	@Test
	public void testWithSquareNumber() {
		float[][] samples = s.makeSamples(4, 2);
		assertEquals(samples.length, 4);
		assertEquals(1/4f, samples[0][0], epsilon);
		assertEquals(1/4f, samples[0][1], epsilon);
		assertEquals(3/4f, samples[1][0], epsilon);
		assertEquals(1/4f, samples[1][1], epsilon);
		assertEquals(1/4f, samples[2][0], epsilon);
		assertEquals(3/4f, samples[2][1], epsilon);
		assertEquals(3/4f, samples[3][0], epsilon);
		assertEquals(3/4f, samples[3][1], epsilon);
	}
	
	@Test
	public void testWithNonSquareNumber() {
		float[][] samples = s.makeSamples(3, 2);
		assertEquals(samples.length, 4);
		assertEquals(1/4f, samples[0][0], epsilon);
		assertEquals(1/4f, samples[0][1], epsilon);
		assertEquals(3/4f, samples[1][0], epsilon);
		assertEquals(1/4f, samples[1][1], epsilon);
		assertEquals(1/4f, samples[2][0], epsilon);
		assertEquals(3/4f, samples[2][1], epsilon);
		assertEquals(3/4f, samples[3][0], epsilon);
		assertEquals(3/4f, samples[3][1], epsilon);
	}

}
