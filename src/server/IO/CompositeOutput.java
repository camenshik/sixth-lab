package server.IO;

import java.io.IOException;

public interface CompositeOutput {
    void outputComposite(String data) throws IOException;
    void outputComposite(String[] data) throws IOException;
    void endOutputComposite(boolean requestMore) throws IOException;
}
