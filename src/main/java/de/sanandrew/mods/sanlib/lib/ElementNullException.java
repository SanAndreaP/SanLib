package de.sanandrew.mods.sanlib.lib;

public class ElementNullException
        extends Exception
{
    static final long serialVersionUID = -8176258322436164166L;

    /**
     * Constructs an {@code ElementNullException} with {@code null}
     * as its error detail message.
     */
    public ElementNullException() {
        super();
    }

    /**
     * Constructs an {@code ElementNullException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ElementNullException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ElementNullException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ElementNullException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ElementNullException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ElementNullException(Throwable cause) {
        super(cause);
    }
}
