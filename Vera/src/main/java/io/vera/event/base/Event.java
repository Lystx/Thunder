
package io.vera.event.base;

import io.vera.event.annotations.Supertype;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.concurrent.NotThreadSafe;


@NotThreadSafe @Getter @Supertype
@Setter
public class Event {

    private boolean cancelled = false;

}