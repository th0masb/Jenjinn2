/**
 *
 */
package jenjinn.enums.chesspiece;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jenjinn.base.FileUtils;
import jenjinn.base.Square;
import jenjinn.pieces.ChessPieces;
import jenjinn.pieces.Piece;
import jenjinn.utils.BasicPieceLocations;
import jflow.seq.Seq;

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
		Seq<Piece> nonPawns = ChessPieces.ALL.filter(p -> !p.isPawn());
		Square.ALL.forEach(square -> testMovesAgreeAtSquare(square, nonPawns));
	}

	@Test
	void testPawnMoves()
	{
		Seq<Piece> pawns = Seq.of(Piece.WHITE_PAWN, Piece.BLACK_PAWN);
		Square.ALL.drop(8).take(48).forEach(square -> testMovesAgreeAtSquare(square, pawns));
	}

	void testMovesAgreeAtSquare(Square square, Seq<Piece> piecesToTest)
	{
		piecesToTest.stream().forEach(piece ->
		{
			FileUtils.cacheResource(getClass(), INPUT_FILE_NAME)
			.map(BasicPieceLocations::reconstructFrom)
			.forEach(locations -> testMovesAreCorrect(piece, square, locations));
		});
	}

	void testMovesAreCorrect(Piece piece, Square square, BasicPieceLocations pieceLocations)
	{
		TestChessPiece constraintPiece = TestChessPiece.values()[piece.ordinal()];
		long white = pieceLocations.getWhite(), black = pieceLocations.getBlack();

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
