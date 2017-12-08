package ndfs.mcndfs_3_opt_4;

import ndfs.NDFS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implements the {@link NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private ArrayList<Worker> workers = new ArrayList<Worker>();
    private AtomicBoolean cycleIsFound = new AtomicBoolean(false);
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
            this.workers.add(new Worker(promelaFile, i, cycleIsFound));
    }

    @Override
    public boolean ndfs(){
        for(Worker worker : workers)
            worker.start();
        for(Worker worker : workers){
            try {
                if(cycleIsFound.get())
                    worker.interrupt();
                worker.join();
            } catch (InterruptedException e) { }
        }
        return cycleIsFound.get();
    }
}
