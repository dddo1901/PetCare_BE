package TechWiz.veterinarian.services;

import TechWiz.veterinarian.dto.VeterinarianMedicalRecordRequest;
import TechWiz.veterinarian.dto.VeterinarianMedicalRecordResponse;
import TechWiz.veterinarian.models.VeterinarianMedicalRecord;
import TechWiz.veterinarian.repositories.VeterinarianMedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VeterinarianMedicalRecordService {
    
    @Autowired
    private VeterinarianMedicalRecordRepository medicalRecordRepository;
    
    // Create new medical record
    public VeterinarianMedicalRecordResponse createMedicalRecord(VeterinarianMedicalRecordRequest request, Long vetId) {
        VeterinarianMedicalRecord record = new VeterinarianMedicalRecord();
        record.setVetId(vetId);
        record.setPetId(request.getPetId());
        record.setOwnerId(request.getOwnerId());
        record.setAppointmentId(request.getAppointmentId());
        record.setType(VeterinarianMedicalRecord.RecordType.valueOf(request.getType()));
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setMedication(request.getMedication());
        record.setNotes(request.getNotes());
        record.setFollowUpInstructions(request.getFollowUpInstructions());
        record.setTreatmentCost(request.getTreatmentCost());
        record.setRecordDate(request.getRecordDate());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        VeterinarianMedicalRecord savedRecord = medicalRecordRepository.save(record);
        return convertToResponse(savedRecord);
    }
    
    // Get all medical records for a vet
    public List<VeterinarianMedicalRecordResponse> getMedicalRecordsByVet(Long vetId) {
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findByVetIdOrderByRecordDateDesc(vetId);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get medical records with pagination
    public Page<VeterinarianMedicalRecordResponse> getMedicalRecordsByVet(Long vetId, Pageable pageable) {
        Page<VeterinarianMedicalRecord> records = medicalRecordRepository.findByVetIdOrderByRecordDateDesc(vetId, pageable);
        return records.map(this::convertToResponse);
    }
    
    // Get medical records by pet ID
    public List<VeterinarianMedicalRecordResponse> getMedicalRecordsByPet(Long vetId, Long petId) {
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findByVetIdAndPetIdOrderByRecordDateDesc(vetId, petId);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get medical records by appointment ID
    public List<VeterinarianMedicalRecordResponse> getMedicalRecordsByAppointment(Long appointmentId) {
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findByAppointmentIdOrderByRecordDateDesc(appointmentId);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get medical records by type
    public List<VeterinarianMedicalRecordResponse> getMedicalRecordsByType(Long vetId, String type) {
        VeterinarianMedicalRecord.RecordType recordType = VeterinarianMedicalRecord.RecordType.valueOf(type);
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findByVetIdAndTypeOrderByRecordDateDesc(vetId, recordType);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get recent medical records
    public List<VeterinarianMedicalRecordResponse> getRecentMedicalRecords(Long vetId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findRecentMedicalRecords(vetId, since);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Update medical record
    public VeterinarianMedicalRecordResponse updateMedicalRecord(Long recordId, VeterinarianMedicalRecordRequest request, Long vetId) {
        VeterinarianMedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        
        if (!record.getVetId().equals(vetId)) {
            throw new RuntimeException("Unauthorized to update this medical record");
        }
        
        record.setType(VeterinarianMedicalRecord.RecordType.valueOf(request.getType()));
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setMedication(request.getMedication());
        record.setNotes(request.getNotes());
        record.setFollowUpInstructions(request.getFollowUpInstructions());
        record.setTreatmentCost(request.getTreatmentCost());
        record.setRecordDate(request.getRecordDate());
        record.setUpdatedAt(LocalDateTime.now());
        
        VeterinarianMedicalRecord savedRecord = medicalRecordRepository.save(record);
        return convertToResponse(savedRecord);
    }
    
    // Delete medical record
    public boolean deleteMedicalRecord(Long recordId, Long vetId) {
        VeterinarianMedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        
        if (!record.getVetId().equals(vetId)) {
            throw new RuntimeException("Unauthorized to delete this medical record");
        }
        
        medicalRecordRepository.delete(record);
        return true;
    }
    
    // Search medical records by diagnosis
    public List<VeterinarianMedicalRecordResponse> searchMedicalRecordsByDiagnosis(Long vetId, String diagnosis) {
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findByVetIdAndDiagnosisContainingIgnoreCase(vetId, diagnosis);
        return records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get medical record statistics
    public Map<String, Object> getMedicalRecordStatistics(Long vetId) {
        Long totalRecords = medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.EXAMINATION) +
                           medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.TREATMENT) +
                           medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.SURGERY) +
                           medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.VACCINATION) +
                           medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.FOLLOW_UP);
        
        Double totalRevenue = medicalRecordRepository.calculateTotalRevenueByVetId(vetId);
        Double monthlyRevenue = medicalRecordRepository.calculateMonthlyRevenueByVetId(vetId, 
                LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
        
        return Map.of(
            "totalRecords", totalRecords,
            "examinations", medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.EXAMINATION),
            "treatments", medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.TREATMENT),
            "surgeries", medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.SURGERY),
            "vaccinations", medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.VACCINATION),
            "followUps", medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.FOLLOW_UP),
            "totalRevenue", totalRevenue != null ? totalRevenue : 0.0,
            "monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0
        );
    }
    
    // Convert entity to response DTO
    private VeterinarianMedicalRecordResponse convertToResponse(VeterinarianMedicalRecord record) {
        VeterinarianMedicalRecordResponse response = new VeterinarianMedicalRecordResponse();
        response.setId(record.getId());
        response.setVetId(record.getVetId());
        response.setPetId(record.getPetId());
        response.setOwnerId(record.getOwnerId());
        response.setAppointmentId(record.getAppointmentId());
        response.setType(record.getType().name());
        response.setDiagnosis(record.getDiagnosis());
        response.setTreatment(record.getTreatment());
        response.setMedication(record.getMedication());
        response.setNotes(record.getNotes());
        response.setFollowUpInstructions(record.getFollowUpInstructions());
        response.setTreatmentCost(record.getTreatmentCost());
        response.setRecordDate(record.getRecordDate());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        
        // TODO: Populate pet, owner, and vet information from respective services
        // This would require additional service calls to get the full information
        
        return response;
    }
}
