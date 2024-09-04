import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Hackerverse.bean.Idea;
import com.Hackerverse.bean.User;
import com.Hackerverse.repo.IdeaRepository;
import com.Hackerverse.repo.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class IdeaService {
    @Autowired
    private IdeaRepository repo;
    
    @Autowired
    private UserRepository userRepo;
    
    public List<Idea> getAllIdea(HttpSession session) {
        User panelist = (User) session.getAttribute("currentPanelist");
        return repo.findByPanelistIdAndStatus(panelist.getUserId(), "Pending");
    }
    
    public String acceptIdea(Idea i, HttpSession session) {
        Idea idea = repo.findById(i.getIdeaId()).get();
        idea.setStatus("Accepted");
        repo.save(idea);
        return "Idea Accepted";
    }
    
    public String rejectIdea(Idea i, HttpSession session) {
        Idea idea = repo.findById(i.getIdeaId()).get();
        idea.setStatus("Rejected");
        repo.save(idea);
        return "Idea Rejected";
    }
    
    public List<Idea> getApprovedIdea() {
        List<Idea> ideas = repo.findAll();
        List<Idea> res = new ArrayList<>();
        for (Idea i : ideas) {
            if (i.getStatus().equals("Accepted")) {
                res.add(i);
            }
        }
        return res;
    }
    
    public List<User> getAllPanelists() {
        return userRepo.findByRole("Panelist");
    }
    
    public void assignPanelistsRandomlyToIdeas() {
        List<User> panelists = getAllPanelists();
        List<Idea> ideas = repo.findByStatus("Pending");
        
        if (panelists.isEmpty() || ideas.isEmpty()) {
            return;
        }
        
        Random random = new Random();
        int numPanelists = panelists.size();
        
        for (Idea idea : ideas) {
            int randomIndex = random.nextInt(numPanelists);
            User selectedPanelist = panelists.get(randomIndex);
            idea.setPanelistId(selectedPanelist.getUserId());
            repo.save(idea);
        }
    }
}
