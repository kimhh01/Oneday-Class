package kr.co.oneclass.author.classbasic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassMaterialDTO {

    private int materialCode;
    private int classCode;
    private String materialName;
    private String materialContent;
}
