/**
 *
 */
package com.github.maumay.jenjinn.entity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.maumay.jenjinn.base.FileUtils;
import com.github.maumay.jenjinn.boardstate.BoardState;
import com.github.maumay.jenjinn.moves.ChessMove;
import com.github.maumay.jenjinn.movesearch.TreeSearcher;
import com.github.maumay.jflow.vec.Vec;

/**
 * @author ThomasB
 */
public final class Jenjinn
{
	private final TreeSearcher treeSearcher = new TreeSearcher();
	private final Vec<String> openingFiles;

	private int openingCount = 0;

	public Jenjinn()
	{
		List<String> files = FileUtils.cacheResource(Jenjinn.class, "openingFileNames")
				.toList();
		Collections.shuffle(files); // Different openings each time.
		openingFiles = Vec.copy(files);
	}

	public Optional<ChessMove> calculateBestMove(BoardState state, long timeLimit)
	{
		if (openingCount++ < 5) {
			Optional<ChessMove> openingSearch = findMoveInOpeningdatabase(state);
			if (openingSearch.isPresent()) {
				openingCount = 0;
				// Lets check that the opening move is ok...
				BoardState cpy = state.copy();
				openingSearch.get().makeMove(cpy);
				try {
					int qsearch = treeSearcher.getQuiescent().search(cpy);
					if (qsearch > 300) {
						System.out.println(
								"Potential hash collision? Aborting opening db and switching to calculation.");
						openingCount = 5;
					} else {
						return openingSearch;
					}
				} catch (InterruptedException e) {
					throw new RuntimeException();
				}
			}
		}
		return treeSearcher.getBestMoveFrom(state, timeLimit);
	}

	private Optional<ChessMove> findMoveInOpeningdatabase(BoardState state)
	{
		for (String openingName : openingFiles) {
			try (OpeningDatabaseReader fileReader = new OpeningDatabaseReader(
					openingName)) {
				Optional<ChessMove> moveFound = fileReader
						.searchForMove(state.calculateHash());
				if (moveFound.isPresent()) {
					return moveFound;
				}
			} catch (IOException e) {
				System.out.println("Error with file: " + openingName);
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}
}
