package ndfs.mcndfs_3_naive;

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
    private AtomicBoolean foundCycle;
    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int threadNum) throws FileNotFoundException {
        foundCycle = new AtomicBoolean(false);
        for(int i = 0; i < threadNum; ++i)
            this.workers.add(new Worker(promelaFile, i, foundCycle));
    }

    @Override
    public boolean ndfs(){
        boolean result = false;
        for(Worker worker : workers)
            worker.start();
        for(Worker worker : workers){
            try {
                if(!foundCycle.get()) {
                    worker.join();
//                  For optimization
//                    if (!worker.getResult())
//                        return false;
//                    else
//                        return true;
                    result |= worker.getResult();
                } else{
                    return true;
                }
            } catch (InterruptedException e) {
            }
        }
        return result;
    }
}
