package rt;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rt.testscenes.NoisyTextureTestScene;
import util.ImageWriter;

/**
 * The main rendering loop. Provides multi-threading support. The {@link Main#scene} to be rendered
 * is hard-coded here, so you can easily change it. The {@link Main#scene} contains 
 * all configuration information for the renderer.
 */
public class Main {

	/** 
	 * The scene to be rendered.
	 */
	public static Scene scene = new NoisyTextureTestScene();
	public static Point debugPixel;// = new Point(92, 114);
	public static final int windowSize = 0;
		
	/**
	 * A render task represents a rectangular image region that is rendered
	 * by a thread in one chunk.
	 */
	static public class RenderTask implements Runnable
	{
		public int left, right, bottom, top;
		public Integrator integrator;
		public Scene scene;
		public Sampler sampler;
		
		public RenderTask(Scene scene, int left, int right, int bottom, int top) 
		{			
			this.scene = scene;
			this.left = left;
			this.right = right;
			this.bottom = bottom;
			this.top = top;

			// The render task has its own sampler and integrator. This way threads don't 
			// compete for access to a shared sampler/integrator, and thread contention
			// can be reduced. 
			integrator = scene.getIntegratorFactory().make(scene);
			sampler = scene.getSamplerFactory().make();
			sampler.init(left*scene.height + bottom);
		}

		@Override
		public void run() {
			for(int j=bottom; j<top; j++)
			{
				for(int i=left; i<right; i++)
				{											
					float samples[][] = integrator.makePixelSamples(sampler, scene.getSPP());
					//for going in a s through pixels, adapt i here
					int iAdapted;
					if (j % 2 == 1)
						iAdapted = right + left - i - 1;
					else
						iAdapted = i;
					// For all samples of the pixel
					for(int k = 0; k < samples.length; k++)
					{	
						// Make ray
						Ray r = scene.getCamera().makeWorldSpaceRay(iAdapted, j, samples[k]);

						// Evaluate ray0
						Spectrum s = integrator.integrate(r);							
						
						// Write to film
						scene.getFilm().addSample(iAdapted + samples[k][0], j + samples[k][1], s);
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{			
		int taskSize = 32;	// Each task renders a square image block of this size
		int nThreads; 
		if (debugPixel == null)
			nThreads = Runtime.getRuntime().availableProcessors();
		else
			nThreads = 1;	// Number of threads to be used for rendering
				
		int width = scene.getFilm().getWidth();
		int height = scene.getFilm().getHeight();

		scene.prepare();
		
		int nTasks = (int)(Math.ceil(width/(double)taskSize) * Math.ceil(height/(double)taskSize));
		//ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(nThreads);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(nTasks) );
		// Make render tasks, split image into blocks to be rendered by the tasks
		
		if (debugPixel != null) {
			scene.outputFilename += "_DEBUG";
			RenderTask debugTask = new RenderTask(scene, debugPixel.x - windowSize, debugPixel.x + 1 + windowSize, 
														 debugPixel.y - windowSize, debugPixel.y + 1 + windowSize);
			executor.execute(debugTask);
		} else {
			for(int j=0; j < Math.ceil(height/(float)taskSize); j++) {
				for(int i=0; i < Math.ceil(width/(float)taskSize); i++) {
					RenderTask task = new RenderTask(scene, i*taskSize, Math.min((i+1)*taskSize, width), j*taskSize, 
																		Math.min((j+1)*taskSize, height));
					executor.execute(task);
				}
			}
		}

		Timer timer = new Timer();
		timer.reset();
		
		
		// Wait for threads to end
		System.out.printf("Rendering scene %s to file %s: \n", scene.getClass().toString(), scene.outputFilename);
		System.out.printf("0%%                                                50%%                                           100%%\n");
		System.out.printf("|---------|---------|---------|---------|---------|---------|---------|---------|---------|---------\n");
		executor.shutdown();
		int printed = 0;
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int toPrint = (int) (executor.getCompletedTaskCount()/(float)executor.getTaskCount()*100);
			for (; printed < toPrint; printed++) {
				System.out.print("*");
			}
		}
		System.out.println("\nFinishing scene...");
		//scene.getIntegratorFactory().finish(scene);
		
		long time_ms = timer.timeElapsed();
		long time_s = time_ms / 1000;
		long time_min =  time_s / 60;
		String timing_output = String.format("Image computed in %d ms = %d min, %d sec.\n", time_ms, time_min, time_s - time_min*60);
		System.out.print(timing_output);
		
		// Tone map output image and writ to file
		BufferedImage image = scene.getTonemapper().process(scene.getFilm());
		
		ImageWriter.writePng(image, scene.getOutputFilename());
		try {
			PrintWriter writer = new PrintWriter(scene.getOutputFilename()+".txt", "UTF-8");
			writer.print(timing_output);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
