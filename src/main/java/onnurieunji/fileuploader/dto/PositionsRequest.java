package onnurieunji.fileuploader.dto;
import java.util.List;

public class PositionsRequest {
    private List<String> positions;

    public PositionsRequest() {
    }

    public PositionsRequest(List<String> positions) {
        this.positions = positions;
    }

    public List<String> getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "PositionsRequest{" +
                "positions=" + positions +
                '}';
    }
}
