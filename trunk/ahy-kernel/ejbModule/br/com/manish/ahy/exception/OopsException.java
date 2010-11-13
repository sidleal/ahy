package br.com.manish.ahy.exception;

public class OopsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Object[] additionalInformation;

    public OopsException(String message) {
        super(message);
    }

    public OopsException(Throwable cause, String message) {
        super(message, cause);
    }

    public OopsException(String message, Object... additionalInfo) {
        super(message);
        this.additionalInformation = additionalInfo;
    }

    public OopsException(Throwable cause, String message, Object... additionalInfo) {
        super(message, cause);
        this.additionalInformation = additionalInfo;
    }

    public String getFormattedStackTrace() {
        String retorno = "";
        Throwable causa = this.getCause();

        if (causa != null) {
            StackTraceElement[] stack = causa.getStackTrace();
            retorno = causa.toString();

            for (int i = 0; i < stack.length; i++) {
                retorno += stack[i] + "\n";
            }
        }

        return retorno;
    }

    public Object[] getAdditionalInformation() {
        return additionalInformation;
    }

}
