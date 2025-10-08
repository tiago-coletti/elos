document.addEventListener('DOMContentLoaded', () => {
    const carouselTrack = document.querySelector('.carousel-track');
    const nextButton = document.querySelector('.next-button');
    const originalCards = Array.from(document.querySelectorAll('.card'));
    const carouselContainer = document.querySelector('.carousel-container');

    let cardWidth = 0;
    let cardsPerView = 0;
    let currentPosition = 0;
    let currentIndex = 0;
    let autoplayInterval;
    const slideDuration = 3000;
    let isTransitioning = false;
    const CARD_GAP = 30;

    if (!carouselTrack || !nextButton || originalCards.length === 0) {
        console.warn('Carrossel não encontrado ou sem cards. Verifique o HTML.');
        return;
    }

    const calculateLayout = () => {
        const containerPaddingLeft = parseFloat(getComputedStyle(carouselContainer).paddingLeft);
        const containerPaddingRight = parseFloat(getComputedStyle(carouselContainer).paddingRight);
        const visibleWidth = carouselContainer.offsetWidth - (containerPaddingLeft + containerPaddingRight);

        const minCardContentWidth = 340;
        const totalCardUnitWidth = minCardContentWidth + CARD_GAP;

        cardsPerView = Math.floor((visibleWidth + CARD_GAP) / totalCardUnitWidth);
        if (cardsPerView < 1) cardsPerView = 1;

        cardWidth = (visibleWidth - (cardsPerView - 1) * CARD_GAP) / cardsPerView;

        document.querySelectorAll('.carousel-track .card').forEach(card => {
            card.style.width = `${cardWidth}px`;
        });

        const buffer = Math.min(cardsPerView + 1, originalCards.length);
        currentPosition = buffer + currentIndex;
        updateCarouselPosition();
    };

    const setupInfiniteLoop = () => {
        carouselTrack.innerHTML = '';

        const numOriginalCards = originalCards.length;
        if (numOriginalCards === 0) return;

        let buffer = cardsPerView + 1;
        if (buffer > numOriginalCards) {
            buffer = numOriginalCards; // 🔧 evita acessar índice inexistente
        }

        // Clona os últimos cards para o início
        for (let i = numOriginalCards - buffer; i < numOriginalCards; i++) {
            const clone = originalCards[i].cloneNode(true);
            clone.dataset.cloned = 'true';
            carouselTrack.appendChild(clone);
        }

        // Clona os cards originais
        originalCards.forEach(card => {
            carouselTrack.appendChild(card.cloneNode(true));
        });

        // Clona os primeiros cards para o final
        for (let i = 0; i < buffer; i++) {
            const clone = originalCards[i].cloneNode(true);
            clone.dataset.cloned = 'true';
            carouselTrack.appendChild(clone);
        }

        carouselTrack.style.transition = 'none';
        currentPosition = buffer;
        updateCarouselPosition();
        
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                carouselTrack.style.transition = 'transform 0.5s ease-in-out';
            });
        });
    };

    const updateCarouselPosition = () => {
        carouselTrack.style.transform = `translateX(${-currentPosition * (cardWidth + CARD_GAP)}px)`;
    };

    const goToSlide = (direction) => {
        if (isTransitioning) return;
        isTransitioning = true;

        const numOriginalCards = originalCards.length;
        const buffer = Math.min(cardsPerView + 1, numOriginalCards);

        if (direction === 1) {
            currentIndex++;
            currentPosition++;
        }

        updateCarouselPosition();
    };

    carouselTrack.addEventListener('transitionend', () => {
        const numOriginalCards = originalCards.length;
        const buffer = Math.min(cardsPerView + 1, numOriginalCards);

        if (currentIndex >= numOriginalCards) {
            currentIndex = 0;
            currentPosition = buffer;
            carouselTrack.style.transition = 'none';
            updateCarouselPosition();
        }

        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                carouselTrack.style.transition = 'transform 0.5s ease-in-out';
                isTransitioning = false;
            });
        });
    });

    const startAutoplay = () => {
        if (autoplayInterval) clearInterval(autoplayInterval);
        autoplayInterval = setInterval(() => {
            goToSlide(1);
        }, slideDuration);
    };

    const pauseAutoplay = () => {
        clearInterval(autoplayInterval);
    };

    const resumeAutoplay = () => {
        pauseAutoplay();
        setTimeout(startAutoplay, 2000);
    };

    calculateLayout();
    setupInfiniteLoop();
    startAutoplay();

    window.addEventListener('resize', () => {
        pauseAutoplay();
        calculateLayout();
        setupInfiniteLoop();
        resumeAutoplay();
    });

    carouselContainer.addEventListener('mouseenter', pauseAutoplay);
    carouselContainer.addEventListener('mouseleave', resumeAutoplay);

    if (nextButton) {
        nextButton.addEventListener('click', () => {
            goToSlide(1);
            resumeAutoplay();
        });
    }

    // --- Modais ---
    const modalButtons = document.querySelectorAll('.view-details-btn, .help-icon');
    const closeButtons = document.querySelectorAll('.modal .close-button');
    const modals = document.querySelectorAll('.modal');

    modalButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            event.stopPropagation();
            pauseAutoplay();
            const modalId = button.dataset.modalTarget;
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.style.display = 'flex';
            }
        });
    });

    closeButtons.forEach(button => {
        button.addEventListener('click', () => {
            const modal = button.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
                resumeAutoplay();
            }
        });
    });

    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
                resumeAutoplay();
            }
        });
    });
});
