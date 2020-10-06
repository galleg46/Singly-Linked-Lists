import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import edu.uwm.cs351.Particle;
import edu.uwm.cs351.ParticleSeq;
import edu.uwm.cs351.Point;
import edu.uwm.cs351.Vector;


public class TestEfficiency extends TestCase {

	Particle p1 = new Particle(new Point(0,0), new Vector(), 1, Color.BLACK);
	Particle p2 = new Particle(new Point(100,0), new Vector(10,0), 2, Color.BLUE);
	Particle p3 = new Particle(new Point(100,100), new Vector(), 3, Color.GREEN);
	Particle p4 = new Particle(new Point(0,-100), new Vector(10,20), 4, Color.YELLOW);
	Particle p5 = new Particle(new Point(0,100), new Vector(0,-10), 5, Color.RED);
	Particle p6 = new Particle(new Point(66,0), new Vector(6,0), 6, Color.CYAN);
	Particle p7 = new Particle(new Point(0,-707), new Vector(7,11), 7, Color.GRAY);
	Particle p8 = new Particle(new Point(88,88), new Vector(8,8), 8, Color.WHITE);

	Particle p[] = {null, p1, p2, p3, p4, p5, p6, p7, p8};
	
	ParticleSeq s;
	Random r;
	
	@Override
	public void setUp() {
		s = new ParticleSeq();
		r = new Random();
		try {
			assert 1/(int)(p5.getPosition().x()) == 42 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
	}

	private static final int MAX_LENGTH = 1000000;
	private static final int SAMPLE = 100;
	
	public void testLong() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			s.addAfter(p[i%6]);
			s.advance();
		}
		
		int sum = 0;
		s.start();
		for (int j=0; j < SAMPLE; ++j) {
			int n = r.nextInt(MAX_LENGTH/SAMPLE);
			for (int i=0; i < n; ++i) {
				s.advance();
			}
			sum += n;
			assertSame(p[sum%6],s.getCurrent());
		}
	}
	
	private static final int MAX_WIDTH = 100000;
	
	public void testWide() {
		ParticleSeq[] a = new ParticleSeq[MAX_WIDTH];
		for (int i=0; i < MAX_WIDTH; ++i) {
			a[i] = s = new ParticleSeq();
			int n = r.nextInt(SAMPLE);
			for (int j=0; j < n; ++j) {
				s.addAfter(p[j%6]);
				s.advance();
			}
		}
		
		for (int j = 0; j < SAMPLE; ++j) {
			int i = r.nextInt(a.length);
			s = a[i];
			if (s.size() == 0) continue;
			int n = r.nextInt(s.size());
			s.start();
			for (int k=0; k < n; ++k) {
				s.advance();
			}
			assertSame(p[n%6],s.getCurrent());
		}
	}
	
	public void testSize() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, s.size());
			if ((i%2) == 1) {
				s.addAfter(p[i%6]);
			} else {
				s.addBefore(p[i%6]);
			}
		}
	}
	
	public void testStochastic() {
		List<ParticleSeq> ss = new ArrayList<ParticleSeq>();
		ss.add(s);
		int max = 1;
		for (int i=0; i < MAX_LENGTH; ++i) {
			if (r.nextBoolean()) {
				s = new ParticleSeq();
				s.addBefore(p3);
				ss.add(s);
			} else {
				s.addAll(s); // double size of s
				if (s.size() > max) {
					max = s.size();
					// System.out.println("Reached " + max);
				}
			}
		}
	}
}
