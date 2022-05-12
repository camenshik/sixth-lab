package server.IO;

import server.connect.AbstractServer;
import server.response.TypeResponse;

import java.io.IOException;

public class ServerIO implements WorkingWithIO, NonCloseable {
    protected AbstractServer server;

    public ServerIO(AbstractServer server) {
        this.server = server;
    }

    @Override
    public String input() throws IOException {
        return this.server.processingRequest();
    }

    @Override
    public void output(String data) throws IOException {
        this.server.send(TypeResponse.DATA, new String[]{data});
    }

    @Override
    public void output(String[] data) throws IOException {
        this.server.send(TypeResponse.DATA, data);
    }

    @Override
    public void outputComposite(String data) throws IOException {
        this.server.send(TypeResponse.COMPOSITE_DATA, new String[]{data});
    }

    @Override
    public void outputComposite(String[] data) throws IOException {
        this.server.send(TypeResponse.COMPOSITE_DATA, data);
    }

    @Override
    public void endOutputComposite(boolean requestMore) throws IOException {
        if (requestMore) {
            this.server.send(TypeResponse.END_COMPOSITE_DATA, new String[]{"request_more"});
        } else {
            this.server.send(TypeResponse.END_COMPOSITE_DATA, new String[]{});
        }
    }
}
