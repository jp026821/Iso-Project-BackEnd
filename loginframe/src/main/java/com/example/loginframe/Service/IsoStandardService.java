package com.example.loginframe.Service;

import com.example.loginframe.Entity.IsoStandard;
import com.example.loginframe.Repository.IsoStandardRepository;
import com.example.loginframe.dto.IsoStandardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IsoStandardService {

    @Autowired
    private IsoStandardRepository isoStandardRepository;

    public List<IsoStandardDTO> getAllIsoStandared()
    {
        return isoStandardRepository.findAll()
                .stream()
                .map(isoStandared -> new IsoStandardDTO(
                        isoStandared.getIsoCode(),
                        isoStandared.getIsoName(),
                        isoStandared.getIsoId()
                ))
                .collect(Collectors.toList());
    }

    public void addIsoStandard(IsoStandardDTO isoStandardDTO)
    {
        IsoStandard iso = new IsoStandard();

        iso.setIsoCode(isoStandardDTO.getIsoCode());
        iso.setIsoName(isoStandardDTO.getIsoName());

        isoStandardRepository.save(iso);
    }

    public void deleteIsoStandard(long id)
    {
        if(!isoStandardRepository.existsById(id)) {
            throw new RuntimeException("ISO not found");
        }

        isoStandardRepository.deleteById(id);
    }

    public void updateIsoStandard(Long id, IsoStandardDTO dto) {

        IsoStandard iso = isoStandardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ISO not found"));

        iso.setIsoCode(dto.getIsoCode());
        iso.setIsoName(dto.getIsoName());

        isoStandardRepository.save(iso);
    }
}
