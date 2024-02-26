package request_result;

import model.GameData;

import java.util.Collection;

public record GameResult(Collection<GameData> games) {
}
