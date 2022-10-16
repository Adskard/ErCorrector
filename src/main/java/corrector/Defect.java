package corrector;

import enums.DefectType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class Defect {
    private DefectType type;
    private Boolean present;
    private String additionalInfo;

    public Defect(DefectType type, Boolean present, String additionalInfo) {
        this.type = type;
        this.present = present;
        this.additionalInfo = additionalInfo;
    }
}
