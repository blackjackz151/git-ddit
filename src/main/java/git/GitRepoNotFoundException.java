package git;

public class GitRepoNotFoundException extends RuntimeException {

    public GitRepoNotFoundException(String message) {
        super(message);
    }

}
