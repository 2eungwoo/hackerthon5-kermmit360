package hackathon.kermmit360.github.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryCommitDto {
    private String sha;
    private Commit commit;
    private Author author;

    @Getter
    @Setter
    public static class Commit {
        private CommitAuthor author;
        private String message;
    }

    @Getter
    @Setter
    public static class CommitAuthor {
        private String name;
        private String date;
    }

    @Getter
    @Setter
    public static class Author {
        private String login;
        private String avatar_url;
    }

}
