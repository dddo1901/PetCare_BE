package TechWiz.veterinarian.dto;

import jakarta.validation.constraints.*;
import TechWiz.veterinarian.models.VetAppointment.AppointmentStatus;

public class AppointmentUpdateRequest {
    
    private AppointmentStatus status;
    
    @Size(max = 500, message = "Vet notes cannot exceed 500 characters")
    private String vetNotes;
    
    @Size(max = 300, message = "Rejection reason cannot exceed 300 characters")
    private String rejectionReason;
    
    // Constructors
    public AppointmentUpdateRequest() {}
    
    public AppointmentUpdateRequest(AppointmentStatus status) {
        this.status = status;
    }
    
    // Getters and setters
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public String getVetNotes() {
        return vetNotes;
    }
    
    public void setVetNotes(String vetNotes) {
        this.vetNotes = vetNotes;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
