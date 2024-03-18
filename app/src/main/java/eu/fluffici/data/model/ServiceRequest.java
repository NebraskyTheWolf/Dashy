package eu.fluffici.data.model;

public class ServiceRequest {

    private final int size;
    private final int page;

    public ServiceRequest(int size, int page) {
        this.size = size;
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }
}