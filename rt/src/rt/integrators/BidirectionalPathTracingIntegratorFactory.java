package rt.integrators;

import java.awt.image.BufferedImage;
import java.util.List;

import rt.Film;
import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;
import rt.films.BoxFilterFilm;
import rt.tonemappers.ClampTonemapper;
import util.ImageWriter;

public class BidirectionalPathTracingIntegratorFactory implements IntegratorFactory {

	private List<BidirectionalPathTracingIntegrator> integrators;
	private Film combinedLightFilm;
	private boolean combined = false;

	public BidirectionalPathTracingIntegratorFactory(Scene scene) {
		this.combinedLightFilm = new BoxFilterFilm(scene.getFilm().getWidth(), scene.getFilm().getHeight());
	}

	@Override
	public Integrator make(Scene scene) {
		BidirectionalPathTracingIntegrator bdp = new BidirectionalPathTracingIntegrator(scene);
		integrators.add(bdp);
		return bdp;
	}

	@Override
	public void prepareScene(Scene scene) {
		// NOTHING
	}

	public void writeLightImage(String path) {
		combineLightFilms();
		BufferedImage img = new ClampTonemapper().process(combinedLightFilm);
		ImageWriter.writePng(img, path);
	}

	public void addLightImage(Film film) {
		combineLightFilms();
		film.addImage(combinedLightFilm);
	}
	
	private void combineLightFilms(){
		if (combined) {
			for (BidirectionalPathTracingIntegrator i: integrators) {
				combinedLightFilm.addImage(i.getLightFilm());
			}
		}
		combined = true;
	}

	@Override
	public void finish(Scene scene) {
		writeLightImage("../output/testscenes/lightimage");
		addLightImage(scene.getFilm());
	}

}
