/**
 *
 */
package jenjinn.engine.enums.chesspiece;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import jenjinn.engine.base.BoardSquare;
import jenjinn.engine.base.FileUtils;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.pieces.ChessPieces;
import jenjinn.engine.utils.BasicPieceLocations;

/**
 * For each
 *
 * @author ThomasB
 */
class MovementIntegrationTest
{
	static final String INPUT_FILE_NAME = "movementIntegrationTestData";

	@Test
	void testNonPawnMoves()
	{
		final List<ChessPiece> nonPawns = ChessPieces.iterate().filter(p -> !p.isPawn()).toList();
		BoardSquare.iterateAll().forEach(square -> testMovesAgreeAtSquare(square, nonPawns));
	}

	@Test
	void testPawnMoves()
	{
		final List<ChessPiece> pawns = asList(ChessPiece.WHITE_PAWN, ChessPiece.BLACK_PAWN);
		BoardSquare.iterateAll().drop(8).take(48).forEach(square -> testMovesAgreeAtSquare(square, pawns));
	}

	void testMovesAgreeAtSquare(final BoardSquare square, final List<ChessPiece> piecesToTest)
	{
		piecesToTest.stream().forEach(piece ->
		{
			FileUtils.cacheResource(getClass(), INPUT_FILE_NAME)
			.map(BasicPieceLocations::reconstructFrom)
			.forEach(locations -> testMovesAreCorrect(piece, square, locations));
		});
	}

	void testMovesAreCorrect(final ChessPiece piece, final BoardSquare square, final BasicPieceLocations pieceLocations)
	{
		final TestChessPiece constraintPiece = TestChessPiece.values()[piece.ordinal()];
		final long white = pieceLocations.getWhite(), black = pieceLocations.getBlack();

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

	// Debugging the test
//	@SuppressWarnings("unused")
//	public static void main(final String[] args)
//	{
//		final String failString = "SOC:Piece=WHITE_KING, Square=H1, PieceLocations[white:100003001c09101|black:6000a18054000008]";
//		final PieceLocations locs = PieceLocations.reconstructFrom("PieceLocations[white:100003001c09101|black:6000a18054000008]");
//		//		BoardSquare
//		System.out.println(FormatBoard.fromPieceLocations(locs));
//
//		System.out.println(FormatBoard.fromBitboard(ChessPiece.WHITE_KING.getSquaresOfControl(BoardSquare.H1, locs)));
//		System.out.println(FormatBoard.fromBitboard(TestChessPiece.WHITE_KING.getSquaresOfControl(BoardSquare.H1, locs)));
//	}
}
