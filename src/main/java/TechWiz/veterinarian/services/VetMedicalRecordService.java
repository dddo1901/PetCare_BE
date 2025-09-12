package TechWiz.veterinarian.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import TechWiz.veterinarian.dto.MedicalRecordRequest;
import TechWiz.veterinarian.dto.MedicalRecordResponse;
import TechWiz.veterinarian.models.VetMedicalRecord;
import TechWiz.veterinarian.repositories.VetMedicalRecordRepository;

@Service
@Transactional
public class VetMedicalRecordService {
    
    @Autowired
    private VetMedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Create medical record
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        VetMedicalRecord record = new VetMedicalRecord();
        updateRecordFromRequest(record, request);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        VetMedicalRecord savedRecord = medicalRecordRepository.save(record);
        return convertToResponse(savedRecord);
    }
    
    // Update medical record
    public Optional<MedicalRecordResponse> updateMedicalRecord(Long recordId, MedicalRecordRequest request) {
        Optional<VetMedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            VetMedicalRecord record = recordOpt.get();
            updateRecordFromRequest(record, request);
            record.setUpdatedAt(LocalDateTime.now());
            
            VetMedicalRecord savedRecord = medicalRecordRepository.save(record);
            return Optional.of(convertToResponse(savedRecord));
        }
        return Optional.empty();
    }
    
    // Get medical record by ID
    public Optional<MedicalRecordResponse> getMedicalRecordById(Long recordId) {
        Optional<VetMedicalRecord> record = medicalRecordRepository.findById(recordId);
        return record.map(this::convertToResponse);
    }
    
    // Get medical record by appointment ID
    public Optional<MedicalRecordResponse> getMedicalRecordByAppointmentId(Long appointmentId) {
        Optional<VetMedicalRecord> record = medicalRecordRepository.findByAppointmentId(appointmentId);
        return record.map(this::convertToResponse);
    }
    
    // Get medical records by veterinarian
    public List<MedicalRecordResponse> getMedicalRecordsByVeterinarian(Long veterinarianId) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByVeterinarianIdOrderByCreatedAtDesc(veterinarianId);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get medical records by pet
    public List<MedicalRecordResponse> getMedicalRecordsByPet(Long petId) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByPetIdOrderByCreatedAtDesc(petId);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get medical records by pet owner
    public List<MedicalRecordResponse> getMedicalRecordsByPetOwner(Long petOwnerId) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByPetOwnerIdOrderByCreatedAtDesc(petOwnerId);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get recent medical records for pet (last 6 months)
    public List<MedicalRecordResponse> getRecentMedicalRecordsByPet(Long petId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<VetMedicalRecord> records = medicalRecordRepository.findRecentRecordsByPet(petId, sixMonthsAgo);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get medical records by date range
    public List<MedicalRecordResponse> getMedicalRecordsByDateRange(Long veterinarianId, LocalDateTime startDate, LocalDateTime endDate) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByVeterinarianAndDateRange(veterinarianId, startDate, endDate);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get medical records by pet and date range
    public List<MedicalRecordResponse> getMedicalRecordsByPetAndDateRange(Long petId, LocalDateTime startDate, LocalDateTime endDate) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByPetAndDateRange(petId, startDate, endDate);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Search medical records
    public List<MedicalRecordResponse> searchMedicalRecords(String keyword) {
        List<VetMedicalRecord> records = medicalRecordRepository.searchMedicalRecords(keyword);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Find records by diagnosis
    public List<MedicalRecordResponse> getMedicalRecordsByDiagnosis(String diagnosis) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByDiagnosisContainingIgnoreCase(diagnosis);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Find records by symptoms
    public List<MedicalRecordResponse> getMedicalRecordsBySymptoms(String symptom) {
        List<VetMedicalRecord> records = medicalRecordRepository.findBySymptomsContainingIgnoreCase(symptom);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Find records by medication
    public List<MedicalRecordResponse> getMedicalRecordsByMedication(String medication) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByMedicationsContainingIgnoreCase(medication);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Find records by treatment
    public List<MedicalRecordResponse> getMedicalRecordsByTreatment(String treatment) {
        List<VetMedicalRecord> records = medicalRecordRepository.findByTreatmentContainingIgnoreCase(treatment);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get records with follow-up required
    public List<MedicalRecordResponse> getRecordsWithFollowUpRequired() {
        LocalDateTime now = LocalDateTime.now();
        List<VetMedicalRecord> records = medicalRecordRepository.findByFollowUpRequiredTrueAndFollowUpDateAfterOrderByFollowUpDateAsc(now);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get upcoming follow-ups for veterinarian
    public List<MedicalRecordResponse> getUpcomingFollowUps(Long veterinarianId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        List<VetMedicalRecord> records = medicalRecordRepository.findUpcomingFollowUps(veterinarianId, now, futureDate);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Get overdue follow-ups
    public List<MedicalRecordResponse> getOverdueFollowUps() {
        LocalDateTime now = LocalDateTime.now();
        List<VetMedicalRecord> records = medicalRecordRepository.findOverdueFollowUps(now);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Mark follow-up as completed
    public Optional<MedicalRecordResponse> markFollowUpCompleted(Long recordId, String followUpNotes) {
        Optional<VetMedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            VetMedicalRecord record = recordOpt.get();
            record.setFollowUpCompleted(true);
            record.setFollowUpCompletedAt(LocalDateTime.now());
            if (followUpNotes != null && !followUpNotes.trim().isEmpty()) {
                record.setFollowUpNotes(followUpNotes);
            }
            record.setUpdatedAt(LocalDateTime.now());
            
            VetMedicalRecord savedRecord = medicalRecordRepository.save(record);
            return Optional.of(convertToResponse(savedRecord));
        }
        return Optional.empty();
    }
    
    // Get pet's diagnosis history
    public List<DiagnosisHistory> getPetDiagnosisHistory(Long petId) {
        List<Object[]> historyData = medicalRecordRepository.getPetDiagnosisHistory(petId);
        return historyData.stream()
            .map(data -> new DiagnosisHistory((String) data[0], (Long) data[1]))
            .toList();
    }
    
    // Get veterinarian's medical record statistics
    public MedicalRecordStats getVeterinarianMedicalRecordStats(Long veterinarianId) {
        Object[] stats = medicalRecordRepository.getVeterinarianRecordStats(veterinarianId);
        if (stats != null && stats.length >= 3) {
            Long totalRecords = (Long) stats[0];
            Long recordsWithFollowUp = (Long) stats[1];
            Long pendingFollowUps = (Long) stats[2];
            return new MedicalRecordStats(totalRecords, recordsWithFollowUp, pendingFollowUps);
        }
        return new MedicalRecordStats(0L, 0L, 0L);
    }
    
    // Count records by veterinarian
    public long countRecordsByVeterinarian(Long veterinarianId) {
        return medicalRecordRepository.countByVeterinarianId(veterinarianId);
    }
    
    // Count records by pet
    public long countRecordsByPet(Long petId) {
        return medicalRecordRepository.countByPetId(petId);
    }
    
    // Check if appointment has medical record
    public boolean hasAppointmentMedicalRecord(Long appointmentId) {
        return medicalRecordRepository.existsByAppointmentId(appointmentId);
    }
    
    // Delete medical record
    public boolean deleteMedicalRecord(Long recordId) {
        if (medicalRecordRepository.existsById(recordId)) {
            medicalRecordRepository.deleteById(recordId);
            return true;
        }
        return false;
    }
    
    // Get records needing follow-up reminders
    public List<MedicalRecordResponse> getRecordsNeedingFollowUpReminder(int reminderDaysBefore) {
        LocalDateTime reminderStart = LocalDateTime.now().plusDays(reminderDaysBefore);
        LocalDateTime reminderEnd = reminderStart.plusDays(1);
        List<VetMedicalRecord> records = medicalRecordRepository.findRecordsNeedingFollowUpReminder(reminderStart, reminderEnd);
        return records.stream().map(this::convertToResponse).toList();
    }
    
    // Helper methods
    private void updateRecordFromRequest(VetMedicalRecord record, MedicalRecordRequest request) {
        record.setAppointmentId(request.getAppointmentId());
        record.setVeterinarianId(request.getVeterinarianId());
        record.setPetOwnerId(request.getPetOwnerId());
        record.setPetId(request.getPetId());
        record.setSymptoms(request.getSymptoms());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setMedications(request.getMedications());
        record.setTestResults(convertTestResultsToObjectList(request.getTestResults()));
        record.setNotes(request.getNotes());
        record.setFollowUpRequired(request.getFollowUpRequired());
        record.setFollowUpDate(request.getFollowUpDate());
        record.setFollowUpNotes(request.getFollowUpNotes());
    }
    
    private MedicalRecordResponse convertToResponse(VetMedicalRecord record) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setId(record.getId());
        response.setAppointmentId(record.getAppointmentId());
        response.setVeterinarianId(record.getVeterinarianId());
        response.setPetOwnerId(record.getPetOwnerId());
        response.setPetId(record.getPetId());
        response.setSymptoms(record.getSymptoms());
        response.setDiagnosis(record.getDiagnosis());
        response.setTreatment(record.getTreatment());
        response.setMedications(record.getMedications());
        response.setTestResults(convertObjectListToTestResults(record.getTestResults()));
        response.setNotes(record.getNotes());
        response.setFollowUpRequired(record.getFollowUpRequired());
        response.setFollowUpDate(record.getFollowUpDate());
        response.setFollowUpCompleted(record.getFollowUpCompleted());
        response.setFollowUpCompletedAt(record.getFollowUpCompletedAt());
        response.setFollowUpNotes(record.getFollowUpNotes());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }
    
    // Test results conversion methods
    private List<Object> convertTestResultsToObjectList(List<MedicalRecordRequest.TestResult> testResults) {
        if (testResults == null) return null;
        
        try {
            return testResults.stream()
                .map(testResult -> {
                    try {
                        return objectMapper.convertValue(testResult, Object.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<MedicalRecordResponse.TestResult> convertObjectListToTestResults(List<Object> objectList) {
        if (objectList == null) return null;
        
        try {
            return objectList.stream()
                .map(obj -> {
                    try {
                        return objectMapper.convertValue(obj, MedicalRecordResponse.TestResult.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(testResult -> testResult != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
    
    // Helper classes
    public static class DiagnosisHistory {
        private String diagnosis;
        private Long count;
        
        public DiagnosisHistory(String diagnosis, Long count) {
            this.diagnosis = diagnosis;
            this.count = count;
        }
        
        // Getters and setters
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
        
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
    public static class MedicalRecordStats {
        private Long totalRecords;
        private Long recordsWithFollowUp;
        private Long pendingFollowUps;
        
        public MedicalRecordStats(Long totalRecords, Long recordsWithFollowUp, Long pendingFollowUps) {
            this.totalRecords = totalRecords;
            this.recordsWithFollowUp = recordsWithFollowUp;
            this.pendingFollowUps = pendingFollowUps;
        }
        
        // Getters and setters
        public Long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(Long totalRecords) { this.totalRecords = totalRecords; }
        
        public Long getRecordsWithFollowUp() { return recordsWithFollowUp; }
        public void setRecordsWithFollowUp(Long recordsWithFollowUp) { this.recordsWithFollowUp = recordsWithFollowUp; }
        
        public Long getPendingFollowUps() { return pendingFollowUps; }
        public void setPendingFollowUps(Long pendingFollowUps) { this.pendingFollowUps = pendingFollowUps; }
    }
}
