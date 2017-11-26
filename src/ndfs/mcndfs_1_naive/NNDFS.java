package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private ArrayList<Worker> workers = new ArrayList<Worker>();

    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int threadNum) throws FileNotFoundException {
        for(int i = 0; i < threadNum; ++i)
            this.workers.add(new Worker(promelaFile, i));
    }

    @Override
    public boolean ndfs() throws InterruptedException {
        boolean result = true;
        for(Worker worker : workers)
            worker.start();
        for(Worker worker : workers)
            worker.join();
        for(Worker worker : workers)
            result &= worker.getResult();
        return result;
    }
}
