package git;

import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

class ObjectInserter {

    private final Repository repository;

    ObjectInserter(Repository repository) {
        this.repository = repository;
    }

    ObjectId insert(int type, byte[] content) throws IOException {
        org.eclipse.jgit.lib.ObjectInserter objectInserter = repository.newObjectInserter();
        ObjectId result = objectInserter.insert(type, content);
        objectInserter.flush();
        return result;
    }

    ObjectId insert(CommitBuilder commit) throws IOException {
        org.eclipse.jgit.lib.ObjectInserter objectInserter = repository.newObjectInserter();
        ObjectId result = objectInserter.insert(commit);
        objectInserter.flush();
        return result;
    }

}
