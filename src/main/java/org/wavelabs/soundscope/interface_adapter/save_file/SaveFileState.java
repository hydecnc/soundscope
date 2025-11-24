package org.wavelabs.soundscope.interface_adapter.save_file;

public class SaveFileState {
    private boolean success;
    private String errorMessage;

    public void setSuccess(boolean value) {
        this.success = value;
        this.errorMessage = null; // 성공하면 에러 초기화
    }

    public void setError(String message) {
        this.success = false;
        this.errorMessage = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
