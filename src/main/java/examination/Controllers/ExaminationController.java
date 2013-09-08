package examination.Controllers;

import examination.DataLayer.models.Answer;
import examination.QuestionService.AnswerService;
import examination.QuestionService.AnswerServiceImpl;
import examination.QuestionService.ExaminationService;
import examination.QuestionService.models.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;
    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = {"/start.html"}, method = RequestMethod.GET)
    public ModelAndView startExam() {
        ModelAndView modelAndView = new ModelAndView("start");
        return modelAndView;
    }

    @RequestMapping(value = {"/start.html"}, method = RequestMethod.POST)
    public ModelAndView startExam(@RequestParam(value = "courseId", required = true) long courseId,
                                  @RequestParam(value = "studentId", required = true) long studentId) {
        ModelAndView modelAndView = new ModelAndView("next_question");
        modelAndView.addObject("question_info", examinationService.start(studentId, courseId));
        return modelAndView;
    }

    @RequestMapping(value = {"/submit_answer.html"}, method = RequestMethod.POST)
    public ModelAndView submitAnswer(Answer answer) {
        answerService.save(answer);
        ModelAndView modelAndView = new ModelAndView("next_question");
        QuestionInfo questionInfo = examinationService.next(answer.getExamId());
        if (questionInfo.getQuestion() != null) {
            modelAndView.addObject("question_info", questionInfo);
            return modelAndView;
        } else {
            return finishExam();
        }
    }

    @RequestMapping(value = {"/finish_exam.html"}, method = RequestMethod.GET)
    public ModelAndView finishExam() {
        //examinationService.finish();
        ModelAndView modelAndView = new ModelAndView("finish_exam");
        return modelAndView;
    }

    /*@RequestMapping(value = {"/exam_question.html"}, method = RequestMethod.GET)
    public ModelAndView nextQuestion(@RequestParam(value = "exam_id",
            required = true) long examId) {
        ModelAndView modelAndView = new ModelAndView("next_question");
        modelAndView.addObject("question_info", examinationService.next(examId));
        return modelAndView;
    }*/
}
