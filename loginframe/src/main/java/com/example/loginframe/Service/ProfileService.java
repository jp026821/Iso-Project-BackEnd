package com.example.loginframe.Service;

import com.example.loginframe.Entity.OrganizationEntity;
import com.example.loginframe.Entity.ProfileEntity;
import com.example.loginframe.Entity.ProfileOrganizationRequest;
import com.example.loginframe.Repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // ✅ CREATE OR UPDATE PROFILE (UPSERT)
    public String saveOrUpdateProfile(ProfileOrganizationRequest porequest) {

        if (porequest == null || porequest.getProfile() == null) {
            throw new IllegalArgumentException("Profile data is required");
        }

        String loginEmail = porequest.getProfile().getLoginEmail();
        if (loginEmail == null || loginEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("loginEmail is required");
        }

        // ✅ If exists -> update, else -> create new
        ProfileEntity profile = profileRepository.findByLoginEmail(loginEmail)
                .orElseGet(ProfileEntity::new);

        // ✅ Set profile fields
        profile.setLoginEmail(loginEmail);
        profile.setNotificationEmail(porequest.getProfile().getNotificationEmail());
        profile.setFirstName(porequest.getProfile().getFirstName());
        profile.setLastName(porequest.getProfile().getLastName());
        profile.setDisplayName(porequest.getProfile().getDisplayName());
        profile.setOrganizationSize(porequest.getProfile().getOrganizationSize());
        profile.setMobile(porequest.getProfile().getMobile());
        profile.setPhone(porequest.getProfile().getPhone());
        profile.setFax(porequest.getProfile().getFax());
        profile.setIndustry(porequest.getProfile().getIndustry());
        profile.setJobTitle(porequest.getProfile().getJobTitle());
        profile.setLanguage(porequest.getProfile().getLanguage());

        // ✅ Organization create/update
        if (porequest.getOrganization() != null) {

            OrganizationEntity org = profile.getOrganization();

            // If no org exists, create new and attach to profile
            if (org == null) {
                org = new OrganizationEntity();
                org.setProfile(profile);
                profile.setOrganization(org);
            }

            org.setCompany(porequest.getOrganization().getCompany());
            org.setAddress(porequest.getOrganization().getAddress());
            org.setCity(porequest.getOrganization().getCity());
            org.setState(porequest.getOrganization().getState());
            org.setCountry(porequest.getOrganization().getCountry());
            org.setPostalCode(porequest.getOrganization().getPostalCode());

        } else {
            // If you want to REMOVE organization when frontend sends null:
            // profile.setOrganization(null);
            // (because orphanRemoval=true, org row will be deleted)
        }

        profileRepository.save(profile);

        return "Profile saved/updated successfully ✅";
    }

    // ✅ GET PROFILE BY LOGIN EMAIL (for frontend auto-fill)
    public ProfileEntity getByLoginEmail(String loginEmail) {
        return profileRepository.findByLoginEmail(loginEmail)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}
