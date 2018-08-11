/**
 *
 */
package jenjinn.fx;

import java.util.function.Function;

import javafx.scene.image.Image;
import jenjinn.engine.base.FileUtils;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FlowList;

/**
 * @author ThomasB
 */
public enum ImageCache
{
	INSTANCE;

	private final FlowList<Image> pieceImages;

	private ImageCache()
	{
		final Function<ChessPiece, String> nameMap = piece -> {
			final String[] xs = piece.name().split("_");
			final char[] chars = {xs[0].charAt(0), piece.isKnight()? 'N' : xs[1].charAt(0)};
			return new String(chars) + "64.png";
		};

		pieceImages = ChessPieces.iterate()
				.map(p -> FileUtils.absoluteName(getClass(), nameMap.apply(p)))
				.map(getClass()::getResourceAsStream)
				.map(Image::new)
				.toList();
	}

	public Image getImageOf(ChessPiece piece)
	{
		return pieceImages.get(piece.ordinal());
	}
}
