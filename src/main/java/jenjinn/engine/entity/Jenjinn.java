/**
 *
 */
package jenjinn.engine.entity;

import static java.util.stream.Collectors.toCollection;
import static jenjinn.engine.utils.FileUtils.loadResourceFromPackageOf;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.movesearch.TreeSearcher;
import jenjinn.engine.openings.OpeningDatabaseReader;
import xawd.jflow.collections.FlowList;
import xawd.jflow.collections.impl.FlowArrayList;

/**
 * @author ThomasB
 */
public final class Jenjinn
{
	private final TreeSearcher treeSearcher = new TreeSearcher();
	private final FlowList<String> openingFiles;

	private int openingCount = 0;

	public Jenjinn()
	{
		final FlowList<String> files = loadResourceFromPackageOf(Jenjinn.class, "openingFileNames")
				.collect(toCollection(FlowArrayList::new));

		Collections.shuffle(files); // Different openings each time.
		openingFiles = files.flow().toImmutableList();
	}

	public Optional<ChessMove> calculateBestMove(BoardState state)
	{
		if (openingCount++ < 5) {
			final Optional<ChessMove> openingSearch = findMoveInOpeningdatabase(state);
			if (openingSearch.isPresent()) {
				openingCount = 0;
				return openingSearch;
			}
		}
		return treeSearcher.getBestMoveFrom(state);
	}

	private Optional<ChessMove> findMoveInOpeningdatabase(BoardState state)
	{
		for (final String openingName : openingFiles) {
			try (final OpeningDatabaseReader fileReader = new OpeningDatabaseReader(openingName)) {
				final Optional<ChessMove> moveFound = fileReader.searchForMove(state.calculateHash());
				if (moveFound.isPresent()) {
					return moveFound;
				}
			} catch (final IOException e) {
				System.out.println("Error with file: " + openingName);
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}
}
