/**
 *
 */
package jenjinn.fx;

import java.util.function.Function;

import javafx.scene.image.Image;
import jenjinn.engine.base.FileUtils;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import xawd.jflow.collections.FList;

/**
 * @author ThomasB
 */
public enum ImageCache
{
	INSTANCE;

	private final FList<Image> pieceImages;

	private ImageCache()
	{
		Function<ChessPiece, String> nameMap = piece -> {
			String[] xs = piece.name().split("_");
			char[] chars = {xs[0].charAt(0), piece.isKnight()? 'N' : xs[1].charAt(0)};
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
