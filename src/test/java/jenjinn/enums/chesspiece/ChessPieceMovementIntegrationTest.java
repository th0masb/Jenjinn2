/**
 *
 */
package jenjinn.enums.chesspiece;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import jenjinn.engine.FileUtils;
import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.ChessPiece;
import jenjinn.engine.misc.PieceLocations;

/**
 * For each
 *
 * @author ThomasB
 */
class ChessPieceMovementIntegrationTest
{
	static final String INPUT_FILE_NAME = "movementIntegrationTestData";

	@Test
	void testNonPawnMoves()
	{
		final List<ChessPiece> nonPawns = ChessPiece.iterateAll().filter(p -> !p.isPawn()).toList();
		BoardSquare.iterateAll().forEach(square -> testMovesAgreeAtSquare(square, nonPawns));
	}

	@Test
	void testPawnMoves()
	{
		final List<ChessPiece> pawns = asList(ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN);
		BoardSquare.iterateAll().skip(8).take(48).forEach(square -> testMovesAgreeAtSquare(square, pawns));
	}

	void testMovesAgreeAtSquare(BoardSquare square, List<ChessPiece> piecesToTest)
	{
		piecesToTest.stream()
		.forEach(piece ->
		{
			FileUtils.loadResourceFromPackageOf(getClass(), INPUT_FILE_NAME)
			.map(PieceLocations::reconstructFrom)
			.forEach(locations -> testMovesAreCorrect(piece, square, locations));
		});
	}

	void testMovesAreCorrect(ChessPiece piece, BoardSquare square, PieceLocations pieceLocations)
	{
		final TestChessPiece constraintPiece = TestChessPiece.values()[piece.ordinal()];
		final long white = pieceLocations.getWhiteLocations(), black = pieceLocations.getBlackLocations();

		assertEquals(
				constraintPiece.getSquaresOfControl(square, white, black),
				piece.getSquaresOfControl(square, white, black),
				"SOC:Piece=" + piece.name() + ", Square=" + square.name() + ", " + pieceLocations.toString()
				);

		assertEquals(
				constraintPiece.getAttacks(square, white, black),
				piece.getAttacks(square, white, black),
				"Attacks:Piece=" + piece.name() + ", Square=" + square.name() + ", " + pieceLocations.toString()
				);

		assertEquals(
				constraintPiece.getMoves(square, white, black),
				piece.getMoves(square, white, black),
				"Moves:Piece=" + piece.name() + ", Square=" + square.name() + ", " + pieceLocations.toString()
				);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		final String failString = "Attacks:Piece=BLACK_PAWN, Square=H2, PieceLocations[white:10008102d084002|black:208204810002540]";
		final PieceLocations locs = PieceLocations.reconstructFrom("PieceLocations[white:10008102d084002|black:208204810002540]");
		//		BoardSquare
	}
}
