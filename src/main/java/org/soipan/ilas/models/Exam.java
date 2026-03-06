package org.soipan.ilas.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Exam {
    private int ExamId;
    private int courseId;
    private int maxScore;
}
