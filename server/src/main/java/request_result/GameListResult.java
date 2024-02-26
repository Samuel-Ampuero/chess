package request_result;

import model.GameData;

import java.util.Collection;

public record GameListResult(Collection<GameResult> games) {
}
