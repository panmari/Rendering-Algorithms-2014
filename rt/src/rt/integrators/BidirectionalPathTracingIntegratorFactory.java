package rt.integrators;

import java.awt.image.BufferedImage;

import rt.Film;
import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;
import rt.Spectrum;
import rt.films.BoxFilterFilm;
import rt.tonemappers.ClampTonemapper;
import util.ImageWriter;

public class BidirectionalPathTracingIntegratorFactory implements IntegratorFactory {


	private Film lightFilm;

	public BidirectionalPathTracingIntegratorFactory(Scene scene) {
		int width = scene.getFilm().getWidth();
		int height = scene.getFilm().getHeight();
		lightFilm = new BoxFilterFilm(width,height);
	}

	@Override
	public Integrator make(Scene scene) {
		return new BidirectionalPathTracingIntegrator(scene);
	}

	@Override
	public void prepareScene(Scene scene) {
		// NOTHING
	}

	public void writeLightImage(String path) {
		BufferedImage img = new ClampTonemapper().process(lightFilm);
		ImageWriter.writePng(img, path);
	}

	public void addLightImage(Film film) {
		film.addImage(lightFilm);
	}

}
