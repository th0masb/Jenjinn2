/**
 *
 */
package jenjinn.engine.entity;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import jenjinn.engine.base.FileUtils;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TreeSearcher;
import xawd.jflow.collections.FList;
import xawd.jflow.collections.Lists;

/**
 * @author ThomasB
 */
public final class Jenjinn
{
	private final TreeSearcher treeSearcher = new TreeSearcher();
	private final FList<String> openingFiles;

	private int openingCount = 0;

	public Jenjinn()
	{
		FList<String> files = Lists.copyMutable(FileUtils.cacheResource(Jenjinn.class, "openingFileNames"));
		Collections.shuffle(files); // Different openings each time.
		openingFiles = files.flow().toList();
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
						System.out.println("Potential hash collision? Aborting opening db and switching to calculation.");
						openingCount = 5;
					}
					else {
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
			try (OpeningDatabaseReader fileReader = new OpeningDatabaseReader(openingName)) {
				Optional<ChessMove> moveFound = fileReader.searchForMove(state.calculateHash());
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
