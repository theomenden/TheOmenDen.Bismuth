package com.theomenden.bismuth.models.debug;

import com.theomenden.bismuth.models.enums.InternalEventType;
import com.theomenden.bismuth.models.records.Coordinates;
import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
public final class DebugEvent {
    @Getter
    @Setter
    private InternalEventType debugType;
    @Getter
    @Setter
    private long profileStartTime;
    @Getter
    @Setter
    private long profileEndTime;
    @Getter
    @Setter
    private Coordinates chunkCoordinates;
    @Getter
    @Setter
    private int colorType;

}
