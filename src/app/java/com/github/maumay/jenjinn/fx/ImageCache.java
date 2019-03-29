/**
 *
 */
package com.github.maumay.jenjinn.fx;

import java.util.function.Function;

import com.github.maumay.jenjinn.base.FileUtils;
import com.github.maumay.jenjinn.pieces.ChessPieces;
import com.github.maumay.jenjinn.pieces.Piece;
import com.github.maumay.jflow.vec.Vec;

import javafx.scene.image.Image;

/**
 * @author ThomasB
 */
public enum ImageCache
{
	INSTANCE;

	private final Vec<Image> pieceImages;

	private ImageCache()
	{
		Function<Piece, String> nameMap = piece -> {
			String[] xs = piece.name().split("_");
			char[] chars = { xs[0].charAt(0), piece.isKnight() ? 'N' : xs[1].charAt(0) };
			return new String(chars) + "64.png";
		};

		pieceImages = ChessPieces.ALL.iter()
				.map(p -> FileUtils.absoluteName(getClass(), nameMap.apply(p)))
				.map(getClass()::getResourceAsStream).map(Image::new).toVec();
	}

	public Image getImageOf(Piece piece)
	{
		return pieceImages.get(piece.ordinal());
	}
}
