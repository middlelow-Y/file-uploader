package onnurieunji.fileuploader.dto;

import java.util.List;
public class DeleteRequest {
    private List<String> deleteValues;

    public DeleteRequest() {
        System.out.println(deleteValues);
    }

    public DeleteRequest(List<String> deleteValues) {
        this.deleteValues = deleteValues;
    }

    public List<String> getPositions() {
        return deleteValues;
    }

    @Override
    public String toString() {
        return "DeleteRequest{" +
                "deleteValues=" + deleteValues +
                '}';
    }
}
